import {useState, useEffect} from 'react'

import VacanciesList from '../src/ui/VacanciesList'
import {listVacancies} from '../src/api'
import { Vacancy } from '../src/types'
import Filters from '../src/ui/Filters'

interface Props{
  vacancies: Vacancy[]
}

export default function Vacancies(props: Props) {
  const [vacancies, setVacancies] = useState<Vacancy[]>()

  useEffect(() => {
    listVacancies()
    .then(data => setVacancies(data.data))
    .catch(err => console.log(err))
  }, [])
  return (
    <>
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
