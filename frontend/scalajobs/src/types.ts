
export type UUID = string 
export type CreateOrganizationInput = Omit<Organization, "id">

export interface TechStackTag {
  id: number,
  name: string
}

export interface Error {}

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
