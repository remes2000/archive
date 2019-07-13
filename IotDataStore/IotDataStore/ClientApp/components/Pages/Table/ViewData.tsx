import * as React from 'react'
import { connect } from 'react-redux'
import { RouteComponentProps } from 'react-router';
import { ApplicationState } from '../../../store';
import * as TableDataStore from '../../../store/TableData'
import * as TableStore from '../../../store/Table'
import JsonDisplay from '../../JsonDisplay'
import Message from '../../Message'
import { MoveToTheHorizontalMiddle, Loading } from '../../StyledComponents'

interface ViewDataCustomProps {
    table: TableStore.TableState
}

type ViewDataProps =
    & TableDataStore.TableDataState
    & typeof TableDataStore.actionCreators
    & ViewDataCustomProps
    & RouteComponentProps<{}>

class ViewData extends React.Component<ViewDataProps> {
    componentDidUpdate() {
        if (this.props.table.id !== -1 && this.props.items.length === 0)
            this.props.getTableData(this.props.table.id)
    }

    componentWillMount() {
        if (this.props.table.id !== -1 && this.props.items.length == 0)
            this.props.getTableData(this.props.table.id)
    }

    render() {

        const tableHeaders: string[] = Object.keys(this.props.table.tableItemModel)

        return (
            <div>
                {(this.props.error && this.props.items.length === 0) &&
                    <MoveToTheHorizontalMiddle>
                        <Loading color="#22333B" />
                    </MoveToTheHorizontalMiddle>
                }
                {this.props.error &&
                    <Message type="negative" content={this.props.error} />
                }
                {this.props.items.map((record: TableDataStore.DataItemInterface, index: number) => {
                    return (
                        <JsonDisplay objectToDisplay={record.data} />
                    )
                })}
            </div>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: RouteComponentProps<{}>) => {
    return {
        ...state.tableData,
        table: state.table,
        ...ownProps
    }
}

export default connect(mapStateToProps, TableDataStore.actionCreators)(ViewData) as typeof ViewData