import React from 'react'
import { Vacancy as VacancyType } from '../types'
import Vacancy from './Vacancy'

interface Props {
    vacancies?: VacancyType[]
}

const Component = ({vacancies}: Props) => 
  <div className="vacanciesContainer">
      {(vacancies || []).map(vacancy => <Vacancy key={vacancy.id} vacancy={vacancy}/>)}
  </div>

export default Component  
