import React, {useEffect, useState} from 'react'
import ReCAPTCHA from "react-google-recaptcha";
import { UUID, TechStackTag, Organization, CreateOrganizationInput, Currency, OfficePresence, OfficePresenceTypes, Currencies } from '../src/types'
import TextInput from '../src/ui/TextInput'
import NumberInput from '../src/ui/NumberInput'
import TextArea from '../src/ui/TextArea'
import SubmitButton from '../src/ui/SubmitButton'
import { createOrganization, listOrganizations, listTags } from '../src/api'
import { RECAPTCHA_API_KEY } from '../src/constants'

const Component = () => {

    return (
        <>
          <section className="postJobDescription">
               Post your job here.
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
    currency?: Currency,
    officePresence?: OfficePresence,
    chosenTags: TechStackTag[],
    captcha: string
}

interface InputsData {
  officePresenceTypes: typeof OfficePresenceTypes,
  currencies: typeof Currencies,
  tags: TechStackTag[],
  organizations: Organization[]
}

interface FormProps  {
    onSubmit: () => void
}

const defaultInput: Input = {
    organizationId: "-1",
    salaryFrom: 100,
    salaryTo: 120,
    currency: "USD",
    officePresence: "remote",
    description: "default input",
    chosenTags: [],
    captcha: ""
}

const Form = (props: FormProps): React.FunctionComponentElement<FormProps> => {
    const [input, setInput] = useState<Input>(defaultInput)
    const [showCompanyForm, setShowCompanyForm] = useState(false)

    const [inputsData, setInputsData] = useState<InputsData>()

    useEffect(() => {
      Promise
      .all([listTags(), listOrganizations()])
      .then(responses =>
        setInputsData({
          officePresenceTypes: OfficePresenceTypes,
          currencies: Currencies,
          tags: responses[0].data, 
          organizations: responses[1].data
      }))
    }, [])

    console.log(inputsData)

    return(
        <div className="post-job__container">
          <select
            placeholder="Choose your company"
            value={input.organizationId}
            onChange={({target}) => setInput({...input, organizationId: target.value})}
          >
            {inputsData?.organizations.map(org =>
              <option key={org.id} value={org.id}>{org.name}</option>  
            )}
          </select>

          <SubmitButton
            text="Didn't find your company? Register"
            onClick={() => setShowCompanyForm(!showCompanyForm)}
          />

          {showCompanyForm && <CreateOrganizationForm/>}
          
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
          
          <NumberInput
            label="Salary from"
            placeholder=""
            onChange={({target}) => setInput({...input, salaryFrom: parseInt(target.value)})}
          />

          <NumberInput
            label="Salary to"
            placeholder=""
            onChange={({target}) => setInput({...input, salaryTo: parseInt(target.value)})}
          />

          <select
            value={input.officePresence}
            onChange={({target}) => setInput({...input, officePresence: target.value as OfficePresence})}
          >
            {OfficePresenceTypes.map(t =>
              <option key={t} value={t} children={t}/>  
            )}
          </select>
da
          <select 
            className="tags"
            value=""
            onChange={({target}) => {
              const newTagId = parseInt(target.value)

              setInput({
                ...input,
                chosenTags: [
                  ...input.chosenTags,
                  inputsData?.tags.find(t => t.id === newTagId)!
                ]
              })
            }}

          >
            {inputsData?.tags.map(tag => 
              <option value={tag.id} key={tag.id}>
                {tag.name}
              </option>
            )}
          </select>

          {input.chosenTags?.map(tag => <Tag {...tag}/> )}

          <ReCAPTCHA
            sitekey={RECAPTCHA_API_KEY}
            onChange={(captcha: string) => setInput({...input, captcha})}
          />

          <button
           onClick={props.onSubmit} 
           children="Post"
          />
        </div>
    )
}

const Tag = (tag: TechStackTag) =>
  <div key={tag.id}>
    {tag.name}
    <p>X</p>
  </div>

const CreateOrganizationForm = () => {
  const [input, setInput] = useState<CreateOrganizationInput>({name: "", description: "", captcha: ""})

  return(
    <div>
      <TextInput
        placeholder="Company name"
        onChange={({target}) => setInput({...input, name: target.value})}
      />

      <TextInput
        placeholder="Describe what it does"
        onChange={({target}) => setInput({...input, description: target.value})}
      />

      <ReCAPTCHA
        sitekey={RECAPTCHA_API_KEY}
        onChange={(captcha: string) => setInput({...input, captcha})}
      />

      <SubmitButton
        text="Register your company"
        onClick={() => 
          createOrganization(input)
          .then(data => console.log(data.data))
          .catch(err => console.log(err.response.data))
        }
      />
    </div>
  )
}

export default Component
