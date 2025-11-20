import api from './api'

export const shelterService = {
  getMyStaffProfile: async () => {
    const res = await api.get('/staff/me')
    return res.data
  },

  updateMyStaffProfile: async (payload) => {
    const res = await api.put('/staff/me', payload)
    return res.data
  },

  deleteMyStaffProfile: async () => {
    const res = await api.delete('/staff/me')
    return res.data
  },

  getMyShelter: async () => {
    const res = await api.get('/staff/my-shelter')
    return res.data
  },

  updateMyShelter: async (payload) => {
    const res = await api.put('/staff/my-shelter', payload)
    return res.data
  },
}

export default shelterService
