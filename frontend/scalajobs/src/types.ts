
export type UUID = string 

export interface TechStackTag {
  id: number,
  name: string
}

export interface Error {
  reason: string
}

export interface Organization {
  id: UUID,
  name: string,
  description: string
}

export interface Vacancy {
  id: UUID,
  organization: Organization,
  description: string,
  salaryFrom: number,
  salaryTo: number,
  tags: TechStackTag[],
  officePresence: string,
  currency: string,
  contactEmail: string,
  link: string
}

export interface ApplicationMetainfo {}
