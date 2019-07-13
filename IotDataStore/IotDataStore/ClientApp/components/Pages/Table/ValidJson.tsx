import * as React from 'React'
import { connect } from 'react-redux'
import { RouteComponentProps } from 'react-router';
import { ApplicationState } from '../../../store';
import * as TableStore from '../../../store/Table'
import JsonDisplay from '../../JsonDisplay'

interface ValidJsonCustomProps {
    table: TableStore.TableState
}

type ValidJsonProps =
    & ValidJsonCustomProps
    & RouteComponentProps<{}>

class ValidJson extends React.Component<ValidJsonProps>{
    render() {
        return (
            <div>
                <JsonDisplay objectToDisplay={this.props.table.tableItemModel} />
            </div>
        )
    }
}

const mapStateToProps = (state: ApplicationState, ownProps: RouteComponentProps<{}>) => {
    return {
        table: state.table,
        ...ownProps
    }
}

export default connect(mapStateToProps, {})(ValidJson) as typeof ValidJson