import React from 'react'
import { Vacancy } from '../types'

interface Props {
    vacancy: Vacancy
}
export default (props: Props) => <div>Vacancy {props.vacancy.description}</div>
