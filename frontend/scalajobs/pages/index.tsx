import {useState, useEffect} from 'react'

import VacanciesList from '../src/ui/VacanciesList'
import {listVacancies} from '../src/api'
import { TechStackTag, Vacancy } from '../src/types'
import Filters from '../src/ui/Filters'

interface Props{
  vacancies: Vacancy[]
}

interface FiltersInputs {
  tags: TechStackTag[]
}

export default function Vacancies(props: ср) { 
  const [filtersInputs, setFiltersInputs] = useState<FiltersInputs>({tags: []})

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
