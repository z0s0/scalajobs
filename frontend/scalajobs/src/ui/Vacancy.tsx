import React from 'react'
import Link from 'next/link'

import { Vacancy } from '../types'
import Component from './VacanciesList'

interface Props {
    vacancy: Vacancy
}

interface BtnProps {
    href: string
}

export default ({vacancy}: Props) =>
   <div className="vacancy">
      <p>Title: {vacancy}</p>
      <p>Vacancy {vacancy.description}</p>
      <p>Tags: {vacancy.tags.map(t => t.name).join(", ")}</p>
      <p></p>
      <p></p>
      <p></p>
      
      <ReadMoreBtn
        href={`/${vacancy.id}`}
      />
      <ApplyBtn
        href={vacancy.link}
      />
  </div>


const ReadMoreBtn = ({href}: BtnProps) => 
  <Link
    href={href}
    children="Read more"
  />

const ApplyBtn = ({href}: BtnProps) => 
  <a
    children="Apply"
    href={href}
  />  
