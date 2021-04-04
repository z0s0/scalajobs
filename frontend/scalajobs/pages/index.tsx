import Head from 'next/head'

import VacanciesList from '../src/ui/VacanciesList'
import {listVacancies} from '../src/api'
import { Vacancy } from '../src/types'

interface Props{
  vacancies: Vacancy[]
}

export default function Vacancies(props: Props) {

  return (
    <div>
      <Head>
        <title>Scalajobs</title>
      </Head>
      <header>
        <div>
          <button>
            Post Job 
          </button>
        </div>
      </header>

      <VacanciesList vacancies={props.vacancies}/>

      <footer>

      </footer>
    </div>
  )
}

export async function getStaticProps() {
  const res = await listVacancies()
  
  const vacancies: Vacancy[] = res.data

  return {props: {vacancies}}
}
