import React, {useEffect, useState} from 'react'
import { useRouter } from 'next/router'

import { getVacancy } from '../src/api'
import { Vacancy } from '../src/types'

import ApplyButton from '../src/ui/ApplyButton'

const IdPage = () => {
  const router = useRouter()
  const {id} = router.query

  const p = id as string

  const [vacancy, setVacancy] = useState<Vacancy | null>(null)

  useEffect(() => {
    getVacancy(p)
    .then(data => setVacancy(data.data))
    .catch(err => console.log(err))
  }, [])

  return (
    <div className="vacancyPage">
      ID : {vacancy?.id}
      <p>{vacancy?.description}</p>
      <p>{vacancy?.contactEmail}</p>

      <ApplyButton href={vacancy?.link}/>
    </div>
  )
}

export default IdPage
