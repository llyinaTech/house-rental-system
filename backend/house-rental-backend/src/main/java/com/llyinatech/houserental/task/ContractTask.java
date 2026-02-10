package com.llyinatech.houserental.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.model.entity.House;
import com.llyinatech.houserental.model.entity.LeaseContract;
import com.llyinatech.houserental.model.entity.SysMessage;
import com.llyinatech.houserental.service.HouseService;
import com.llyinatech.houserental.service.LeaseContractService;
import com.llyinatech.houserental.service.SysMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ContractTask {

    @Autowired
    private LeaseContractService leaseContractService;

    @Autowired
    private SysMessageService sysMessageService;
    
    @Autowired
    private HouseService houseService;
    
    @Autowired
    private com.llyinatech.houserental.service.RentBillService rentBillService;

    /**
     * 每天上午10点执行，检查即将到期的合同
     */
    @Scheduled(cron = "0 0 10 * * ?")
    // @Scheduled(fixedRate = 60000) // Test: every minute
    public void checkContractExpiry() {
        log.info("Start checking contract expiry...");
        
        // 1. Check contracts expiring in 7 days
        checkExpiringContracts(7);
        
        // 2. Check contracts expiring in 1 day
        checkExpiringContracts(1);
        
        // 3. Generate Rent Bills for upcoming cycle
        generateRentBills();
        
        log.info("Contract expiry check and bill generation finished.");
    }

    private void generateRentBills() {
        log.info("Start generating rent bills...");
        List<LeaseContract> contracts = leaseContractService.list(new LambdaQueryWrapper<LeaseContract>()
                .eq(LeaseContract::getStatus, 1)); // Active

        if (contracts.isEmpty()) {
            return;
        }
        
        List<com.llyinatech.houserental.model.entity.RentBill> newBills = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (LeaseContract contract : contracts) {
            // Calculate next payment dates
            // Assuming startDate is the first payment date (or start of period)
            // Logic: iterate from startDate until endDate, step by payPeriod months
            // If a date is within [today, today + 15 days] and no bill exists, create one.
            
            LocalDate startDate = contract.getStartDate();
            LocalDate endDate = contract.getEndDate();
            int period = contract.getPayPeriod() != null ? contract.getPayPeriod() : 1;
            
            LocalDate nextDue = startDate;
            while (!nextDue.isAfter(endDate)) {
                // If nextDue is within next 15 days (or passed but not billed)
                if (!nextDue.isAfter(today.plusDays(15))) {
                     // Check existence
                     // Use periodDesc as unique key for simplicity: "2025-06-01"
                     String periodDesc = nextDue.toString();
                     
                     long count = rentBillService.count(new LambdaQueryWrapper<com.llyinatech.houserental.model.entity.RentBill>()
                             .eq(com.llyinatech.houserental.model.entity.RentBill::getContractId, contract.getId())
                             .eq(com.llyinatech.houserental.model.entity.RentBill::getDueDate, nextDue));
                     
                     if (count == 0) {
                         com.llyinatech.houserental.model.entity.RentBill bill = new com.llyinatech.houserental.model.entity.RentBill();
                         bill.setContractId(contract.getId());
                         bill.setTenantId(contract.getTenantId());
                         bill.setLandlordId(contract.getLandlordId());
                         bill.setBillAmount(contract.getRentAmount().multiply(new java.math.BigDecimal(period))); // Amount * months
                         bill.setDueDate(nextDue);
                         bill.setPeriodDesc(nextDue.getYear() + "年" + nextDue.getMonthValue() + "月 租金");
                         bill.setPayStatus(0); // Unpaid
                         bill.setReminderCount(0);
                         bill.setCreateTime(LocalDateTime.now());
                         bill.setUpdateTime(LocalDateTime.now());
                         
                         newBills.add(bill);
                     }
                }
                nextDue = nextDue.plusMonths(period);
            }
        }
        
        if (!newBills.isEmpty()) {
            rentBillService.saveBatch(newBills);
            log.info("Generated {} new rent bills.", newBills.size());
            
            // Notify tenants about new bills
            List<SysMessage> messages = new ArrayList<>();
            for (com.llyinatech.houserental.model.entity.RentBill bill : newBills) {
                 SysMessage msg = new SysMessage();
                 msg.setTitle("新租金账单已生成");
                 msg.setContent("您有新的租金账单需要支付，金额：" + bill.getBillAmount() + "元，截止日期：" + bill.getDueDate());
                 msg.setReceiverId(bill.getTenantId());
                 msg.setType("BILL");
                 msg.setStatus(0);
                 msg.setCreateTime(LocalDateTime.now());
                 messages.add(msg);
            }
            sysMessageService.saveBatch(messages);
        }
    }

    private void checkExpiringContracts(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        
        List<LeaseContract> contracts = leaseContractService.list(new LambdaQueryWrapper<LeaseContract>()
                .eq(LeaseContract::getStatus, 1) // Active
                .eq(LeaseContract::getEndDate, targetDate));
        
        if (contracts.isEmpty()) {
            return;
        }

        List<SysMessage> messages = new ArrayList<>();
        
        for (LeaseContract contract : contracts) {
            // Get House Info
            House house = houseService.getById(contract.getHouseId());
            String houseTitle = (house != null) ? house.getTitle() : "未知房源";

            // Notify Tenant
            if (contract.getTenantId() != null) {
                SysMessage msg = new SysMessage();
                msg.setTitle("合同即将到期提醒");
                msg.setContent("您承租的房源 [" + houseTitle + "] 合同将于 " + days + " 天后 (" + targetDate + ") 到期，请及时处理。");
                msg.setReceiverId(contract.getTenantId());
                msg.setType("CONTRACT");
                msg.setStatus(0);
                msg.setCreateTime(LocalDateTime.now());
                messages.add(msg);
            }

            // Notify Landlord
            if (contract.getLandlordId() != null) {
                SysMessage msg = new SysMessage();
                msg.setTitle("合同即将到期提醒");
                msg.setContent("您出租的房源 [" + houseTitle + "] 合同将于 " + days + " 天后 (" + targetDate + ") 到期，请及时联系租客。");
                msg.setReceiverId(contract.getLandlordId());
                msg.setType("CONTRACT");
                msg.setStatus(0);
                msg.setCreateTime(LocalDateTime.now());
                messages.add(msg);
            }
        }
        
        if (!messages.isEmpty()) {
            sysMessageService.saveBatch(messages);
            log.info("Generated {} expiry reminders for contracts expiring in {} days.", messages.size(), days);
        }
    }
}
