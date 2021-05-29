import React from 'react'
import { Vacancy as VacancyType } from '../types'
import Vacancy from './Vacancy'

interface Props {
    vacancies?: VacancyType[]
}

export default (props: Props) => 
  <div className="vacanciesContainer">
      {(props.vacancies || []).map(vacancy => <Vacancy key={vacancy.id} vacancy={vacancy}/>)}
  </div>
