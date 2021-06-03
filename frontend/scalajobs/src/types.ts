
export type UUID = string 
export type CreateOrganizationInput = Omit<Organization, "id"> & {captcha: string}
export type OfficePresence = typeof OfficePresenceTypes[number]
export type Currency = typeof Currencies[number]

export const OfficePresenceTypes = ['remote', 'flexible', 'office'] as const
export const Currencies = ['USD', 'THB', 'EUR', 'RUB']

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
  officePresence: OfficePresence,
  currency: Currency,
  contactEmail: string,
  link: string
}

