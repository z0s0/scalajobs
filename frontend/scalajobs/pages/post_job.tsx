import React, {useEffect, useState} from 'react'
import { UUID, TechStackTag } from '../src/types'
import TextInput from '../src/ui/TextInput'
import NumberInput from '../src/ui/NumberInput'
import TextArea from '../src/ui/TextArea'

export default () => {

    return (
        <>
          <section className="postJobDescription">
               
          </section>

          <Form onSubmit={() => console.log("Submitted")}/>
        </>
    )
}

interface Input {
    description?: string,
    organizationId: UUID,
    salaryFrom?: number,
    salaryTo?: number,
    currency?: string,
    officePresence?: string
}

interface InputsData {
  officePresenceTypes: string[],
  currencies: string[],
  tags: TechStackTag[]
}

interface FormProps  {
    onSubmit: () => void
}

const officePresenceTypes = ["remote", "office", "flexible"]
const defaultInput: Input = {
    organizationId: "-1",
    salaryFrom: 100,
    salaryTo: 120,
    currency: "USD",
    officePresence: "remote",
    description: "default input"
}

const Form = (props: FormProps): React.FunctionComponentElement<FormProps> => {
    const [input, setInput] = useState<Input>(defaultInput)
    console.log(input)

    const [inputsData, setInputsData] = useState<InputsData>()

    useEffect(() => {
        setInputsData({
            officePresenceTypes: officePresenceTypes,
            currencies: ["USD", "RUB", "THB"],
            tags: tags
        })
    }, [])

    return(
        <div className="post-job__container">
          <TextInput
            placeholder=""
            tooltipText="Vacancy link"
            label="Link"
            onChange={() => console.log("")}
          />

        <TextArea
          placeholder="Description"
          onChange={({target}) => setInput({...input, description: target.value})}
        />

        <select 
          value={input?.currency}
          className="input"
          onChange={evt => setInput({...input, currency: evt.target.value})} 
        >
            {
              inputsData?.currencies.map(currency =>
                <option 
                  key={currency}
                  children={currency}
                  value={currency}
                    
                  onChange={thing => console.log(thing)}
                />    
              )
            }
          </select>
          
          

          <button
           onClick={props.onSubmit} 
           children="Post"
          />
        </div>
    )
}

const tags: TechStackTag[] = [
    {id: 1, name: "PostgreSQL"},
    {id: 2, name: "ZIO"}
]
