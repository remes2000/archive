import * as React from 'react'
import { RouteComponentProps } from 'react-router';
import Wysiwyg from '../../Wysiwyg'
import { MoveToTheRight, Button } from '../../StyledComponents'
import { EditorState } from 'draft-js'
import { stateToHTML } from 'draft-js-export-html'

type EditHomepageProps =
    & RouteComponentProps<{}>

interface EditHomepageState {
    editorState: EditorState
}

class EditHomepage extends React.Component<EditHomepageProps, EditHomepageState>{
    constructor() {
        super()

        this.state = {
            editorState: EditorState.createEmpty()
        }
    }

    private onChange = (editorState: EditorState) => this.setState({ editorState })

    private saveChanges = () => {
        const html = stateToHTML(this.state.editorState.getCurrentContent())
    }

    render() {
        return (
            <div>
                <Wysiwyg editorState={this.state.editorState} onChange={this.onChange} />
                <MoveToTheRight>
                    <Button type="button" color="#52a34e" style={{ cursor: 'pointer' }} onClick={this.saveChanges}>
                        Save changes
                    </Button>
                </MoveToTheRight>
            </div>
        )
    }
}

export default EditHomepage