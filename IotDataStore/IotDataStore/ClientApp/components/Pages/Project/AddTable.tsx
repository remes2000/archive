import * as React from 'react'
import { RouteComponentProps } from 'react-router';
import { InputTextField, InputContainer, InputLabel, Button, MoveToTheRight, SelectField, SubmitButton } from '../../StyledComponents'
import styled from 'styled-components'
import AddCross from '../../../resources/svg/add.svg'
import DeleteCross from '../../../resources/svg/delete.svg'
import Message from '../../Message'
import { connect } from 'react-redux'
import { ApplicationState } from '../../../store';
import * as ProjectStore from '../../../store/Project'
import * as TableStatusStore from '../../../store/NewTableStatus'
import { Link } from 'react-router-dom';
import JsonDisplay from '../../JsonDisplay';

type AddTableProps =
    & RouteComponentProps<{}>
    & AddTableCustomProps
    & TableStatusStore.NewTableStatusState
    & typeof TableStatusStore.actionCreators

interface AddTableCustomProps {
    project: ProjectStore.ProjectState
}

interface PropertyField {
    name: string,
    value: string
}

interface Error {
    content: string
}

interface AddTableState {
    properties: PropertyField[],
    tableName: string,
    errors: Error[],
    loading: boolean
}

class AddTable extends React.Component<AddTableProps, AddTableState> {

    constructor() {
        super()
        this.state = {
            properties: [],
            tableName: '',
            errors: [],
            loading: false
        }
    }

    private onPropertyNameChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
        const properties = this.state.properties
        properties[index].name = e.target.value
        this.setState({properties})
    }

    private onPropertyTypeChange = (e: React.ChangeEvent<HTMLSelectElement>, index: number) => {
        const properties = this.state.properties
        properties[index].value = e.target.value
        this.setState({ properties })
    }

    private addProperty = () => {
        const properties = this.state.properties
        properties.push({ name: '', value: 'number' })
        this.setState({properties})
    }

    private deleteProperty = (index: number) => {
        const properties = this.state.properties
        properties.splice(index, 1)
        this.setState({properties})
    }

    private validate = () => {
        let errors: Error[] = []
        if (!this.state.tableName) errors.push({ content: "Table name cannot be empty!" })
        this.state.properties.forEach((property: PropertyField) => {
            if (!property.name || !property.value)
                errors.push({content: "All properties must have an name and type!"})
        })
        if (this.state.properties.length === 0)
            errors.push({content: "Table require at least one property!"})
        return errors
    }

    private createTable = () => {
        const errors = this.validate()
        this.setState({ errors })
        if (errors.length !== 0)
            return

        const tableItemModel: any = {}

        this.state.properties.forEach(p => {
            tableItemModel[p.name] = p.value
        })

        const newTableData: TableStatusStore.NewTableData = {
            projectId: this.props.project.id,
            title: this.state.tableName,
            tableItemModel: JSON.stringify(tableItemModel)
        }

        this.setState({loading: true})

        this.props.createTable(newTableData)
    }

    render() {
        return (
            <div>
                {this.state.errors.map((error: Error) => {
                    return <Message type="negative" content={error.content} />
                })}
                {(this.state.loading && !this.props.status && !this.props.error) &&
                    <Message type="neutral" content="loading..." />
                }
                {this.props.status &&
                    <div>
                        <Message type="positive" content={this.props.status} />
                    <MoveToTheRight>
                        <Link to={`/table/${this.props.id}/project/${this.props.project.id}`} style={{ color: '#2f3237', fontSize: '2rem' }}>Move to table!</Link>
                        </MoveToTheRight>
                    </div>
                }
                {this.props.error &&
                    <Message type="negative" content={this.props.error} />
                }
                <InputContainer>
                    <InputLabel>Table Name</InputLabel>
                    <InputTextField type="text" value={this.state.tableName} onChange={e => this.setState({tableName: e.target.value})} />
                </InputContainer>
                {this.state.properties.map((property: PropertyField, index: number) => {
                    return (
                        <div>
                            <MoveToTheRight>
                                <Button color="#a24d4d" style={{ cursor: 'pointer' }} onClick={() => this.deleteProperty(index)}>
                                    <img src={DeleteCross} alt="delete button" style={{ height: '100%', marginRight: '1rem' }} />
                                    Delete property
                                </Button>
                            </MoveToTheRight>
                            <InputContainer key={`propertyinput${index}`}>
                                <InputLabel>property name</InputLabel>
                                <InputTextField type="text" onChange={e => this.onPropertyNameChange(e, index)} value={this.state.properties[index].name} />
                            </InputContainer>
                            <InputContainer key={`typeinput${index}`}>
                                <InputLabel>property type</InputLabel>
                                <SelectField onChange={e => this.onPropertyTypeChange(e, index)}>
                                    <option value="number">Number</option>
                                    <option value="string">String</option>
                                </SelectField>
                            </InputContainer>
                        </div>
                    )
                })}
                <MoveToTheRight>
                    <Button color="#52a34e" style={{ cursor: 'pointer' }} onClick={this.addProperty}>
                        <img src={AddCross} alt="add button" style={{ height: '100%', marginRight: '1rem' }} />
                        Add property
                    </Button>
                </MoveToTheRight>
                <JsonDisplay arrayToDisplay={this.state.properties} />
                <MoveToTheRight>
                    <SubmitButton type="button" value="create table" style={{ fontSize: '2rem', cursor: 'pointer', border: 0 }} onClick={this.createTable} />
                </MoveToTheRight>
            </div>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: any) => {
    return {
        project: state.project,
        ...state.newTableStatus,
        ...ownProps
    }
}

export default connect(mapStateToProps, TableStatusStore.actionCreators)(AddTable) as typeof AddTable