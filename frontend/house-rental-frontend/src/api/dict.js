import api from '@/api/request'

// 字典类型相关接口

// 查询字典类型列表
export function listType(query) {
  return api.get('/api/dict/type/list', { params: query })
}

// 查询字典类型详细
export function getType(dictId) {
  return api.get('/api/dict/type/' + dictId)
}

// 新增字典类型
export function addType(data) {
  return api.post('/api/dict/type', data)
}

// 修改字典类型
export function updateType(data) {
  return api.put('/api/dict/type', data)
}

// 删除字典类型
export function delType(dictIds) {
  return api.delete('/api/dict/type/' + dictIds)
}

// 字典数据相关接口

// 查询字典数据列表
export function listData(query) {
  return api.get('/api/dict/data/list', { params: query })
}

// 查询字典数据详细
export function getData(dictCode) {
  return api.get('/api/dict/data/' + dictCode)
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return api.get('/api/dict/data/type/' + dictType)
}

// 新增字典数据
export function addData(data) {
  return api.post('/api/dict/data', data)
}

// 修改字典数据
export function updateData(data) {
  return api.put('/api/dict/data', data)
}

// 删除字典数据
export function delData(dictCodes) {
  return api.delete('/api/dict/data/' + dictCodes)
}
