import * as React from 'react';
import NavMenu from './NavMenu';
import { injectGlobal } from 'styled-components'
injectGlobal`
    html{
        font-size: 0.625em;
        font-family: 'Roboto', sans-serif;
    }
    
    body{
        background-color: #2f3237;
        padding: 0;
        margin: 0;
        overflow-x: hidden;
    }

    html, body, #react-app > div{
        width: 100%;
        height: 100%;
    }

    .navigation-list, .navigation-list a{
        text-decoration: none;
        color white;
    }

`

export class Layout extends React.Component<{}, {}> {
    public render() {
        return (
            <div>
                <NavMenu />
                { this.props.children }
            </div>
        )
    }
}
