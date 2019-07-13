import * as React from 'react'
import styled from 'styled-components'
import { ItemModelInterface } from '../store/Table'

interface PropertyField {
    name: string;
    value: string;
} 

interface JsonDisplayProps {
    objectToDisplay?: ItemModelInterface,
    arrayToDisplay?: PropertyField[] 
}

const CodeBlock = styled.pre`
    width: 100%;
    padding: 3rem;
    box-sizing: border-box;
    background-color: #d0d0d0;
    font-size: 1.5rem;
    overflow-x: scroll;
`

export default class JsonDisplay extends React.Component<JsonDisplayProps>{

    private convertObjectToArray = (objectToDisplay: ItemModelInterface): PropertyField[] => {
        let propertiesArray: PropertyField[] = []
        Object.keys(objectToDisplay).forEach((key: string) => {
            let newProperty: PropertyField = { name: key, value: objectToDisplay[key] }
            propertiesArray.push(newProperty)
        })
        return propertiesArray
    }

    render() {

        const { arrayToDisplay, objectToDisplay } = this.props

        let displayContent: PropertyField[] = []

        if (arrayToDisplay)
            displayContent = arrayToDisplay

        if (objectToDisplay)
            displayContent = this.convertObjectToArray(objectToDisplay)

        return (
            <div>
                JSON
                < CodeBlock >
                    <code>
                        &#123;
                        {displayContent.map((property: PropertyField) => {
                            return '\n  \"' + property.name + '\"   :   \"' + property.value + '\"\n'
                        })}
                        &#125;
                    </code>
                </CodeBlock >
            </div>
        )
    }
}