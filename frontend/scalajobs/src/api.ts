
import axios from 'axios'
import { API_URL } from './constants'
import { Vacancy, UUID } from './types'

export const listVacancies = () => 
  axios.get<Vacancy[]>(`${API_URL}/vacancies`)

export const getVacancy = (id: UUID) => 
  axios.get<Vacancy | null>(`${API_URL}/vacancies/${id}`)
