import * as React from 'react'
import styled from 'styled-components'

interface MessageContainerProps {
    type: string;
}

const MessageContainer = styled.div`
    width: 100%;
    padding: 1.5rem;
    box-sizing: border-box;
    background-color: ${(p: MessageContainerProps) => p.type === "positive" ? '#559959' : p.type === "negative" ? '#995555' :'#000000'};
    margin-bottom: 1.5rem;
    border-radius: .2rem;`

const MessageTitle = styled.header`
    font-size: 3rem;
`

const MessageContent = styled.p`
    color: white;
    font-size: 1.5rem;
`

interface MessageProps {
    title?: string;
    content: string;
    type: string;
}

export default class Message extends React.Component<MessageProps>{
    public render() {
        return (
            <MessageContainer type={this.props.type}>
                <MessageTitle>{this.props.title}</MessageTitle>
                <MessageContent>{this.props.content}</MessageContent>
            </MessageContainer>
        )
    }
}