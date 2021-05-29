import Head from 'next/head'
import Link from 'next/link'
import VacanciesList from '../src/ui/VacanciesList'
import {listVacancies} from '../src/api'
import { Vacancy } from '../src/types'
import Filters from '../src/ui/Filters'
import {useState} from 'react'

interface Props{
  vacancies: Vacancy[]
}

const vac = 
  {
    id: "1",
    organization: {id: "1233432", name: "Tinkoff", description: "bank"},
    description: "123",
    salaryFrom: 200,
    salaryTo: 50, 
    currency: "USD",
    tags: [
      {id: 1, name: "ZIO"},
      {id: 2, name: "Cats"},
      {id: 3, name: "PostgreSQL"}, 
      {id: 4, name: "Cassandra"},
      {id: 5, name: "Redis"},
      {id: 6, name: "Kubernetes"}
    ],
    officePresence: "remote",
    link: "site.com", 
    contactEmail: ""
  }

export default function Vacancies(props: Props) {
  const initVacancies = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1].map(_ => vac)
  const [vacancies, setVacancies] = useState<Vacancy[]>(initVacancies)

  return (
    <>
      <Head>
        <title>Scalajobs</title>
      </Head>

      <Filters/>
      <VacanciesList vacancies={vacancies}/>
    </>
  )
}

export async function getStaticProps() {
  const res = await listVacancies()
  
  const vacancies: Vacancy[] = res.data

  console.log(vacancies)

  return {props: {vacancies}}
}
