import * as React from 'react'
import { Editor, EditorState, RichUtils } from 'draft-js'
import styled, { injectGlobal } from 'styled-components'

injectGlobal`
    .DraftEditor-editorContainer{
        border: 0.2rem solid #727272;
        border-radius: 0.2rem;
        padding: 2rem;
        background-color: transparent;
        font-size: 1.5rem;
        margin: 2.5rem 0;
    }
`

const MenuContainer = styled.ul`
    list-style-type: none;
    padding: 0;
    display: flex;
    flex-wrap: wrap;
`

const MenuItem = styled.li`
    font-size: 1.7rem;
    margin: 1rem;
    font-weight: normal;
    cursor: pointer;

    &:hover{
        font-weight: bold;
    }
`

interface WysiwygProps {
    editorState: EditorState;
    onChange: (editorState: EditorState) => void;
}

export default class Wysiwyg extends React.Component<WysiwygProps>{
    constructor() {
        super()
    }

    private bold = () => {
        this.props.onChange(RichUtils.toggleInlineStyle(this.props.editorState, 'BOLD'))
    }

    private italic = () => {
        this.props.onChange(RichUtils.toggleInlineStyle(this.props.editorState, 'ITALIC'))
    }

    private underline = () => {
        this.props.onChange(RichUtils.toggleInlineStyle(this.props.editorState, 'UNDERLINE'))
    }

    render() {
        return (
            <div>
                <MenuContainer>
                    <MenuItem onClick={this.bold}>Bold</MenuItem>
                    <MenuItem onClick={this.italic}>Italic</MenuItem>
                    <MenuItem onClick={this.underline}>Underline</MenuItem>
                </MenuContainer>
                <Editor editorState={this.props.editorState} onChange={this.props.onChange} />
            </div>
        )
    }
}