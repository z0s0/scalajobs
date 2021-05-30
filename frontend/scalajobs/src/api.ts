
import axios from 'axios'
import { API_URL } from './constants'
import { Vacancy, UUID, ApplicationMetainfo, Organization, TechStackTag } from './types'

export const listVacancies = () => 
  axios.get<Vacancy[]>(`${API_URL}/vacancies`)

export const getVacancy = (id: UUID) => 
  axios.get<Vacancy | null>(`${API_URL}/vacancies/${id}`)

export const listTags = () => 
  axios.get<TechStackTag[]>(`${API_URL}/tags`)  

export const listOrganizations = () => 
  axios.get<Organization[]>(`${API_URL}/organizations`)  

export const createOrganization = (payload: {name: string, description: string}) => 
  axios.post<Organization | {reason: string}>(`${API_URL}/organizations`, payload)

export const getApplicationMetainfo = () => 
  axios.get<ApplicationMetainfo>(`${API_URL}/metainfo`)  
