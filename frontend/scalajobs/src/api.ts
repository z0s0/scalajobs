
import axios from 'axios'
import { API_URL } from './constants'
import { Vacancy, UUID, ApplicationMetainfo } from './types'

export const listVacancies = () => 
  axios.get<Vacancy[]>(`${API_URL}/vacancies`)

export const getVacancy = (id: UUID) => 
  axios.get<Vacancy | null>(`${API_URL}/vacancies/${id}`)

export const getApplicationMetainfo = () => 
  axios.get<ApplicationMetainfo>(`${API_URL}/metainfo`)  
