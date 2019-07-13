import * as React from 'react';
import { NavLink, Link } from 'react-router-dom'
import styled, { css } from 'styled-components'
import HamburgerSvg from '../resources/svg/hamburger.svg'
import CloseNavigationSvg from '../resources/svg/x.svg'
import { MoveToTheRight, media } from './StyledComponents'
import { connect } from 'react-redux'
import * as UserStore from '../store/User'

import { ApplicationState } from '../store';
import setAuthorizationHeader from '../utils/setAuthorizationHeader';

const Navigation = styled.nav`
    width: 20vw;
    height: 100vh;
    background-color: #22333B;
    border-left: 0.2rem solid #00283A;
    position: fixed;
    top: 0;
    right: 0;
    transition: .2s transform linear;
    transform: translateX(${props => props.open ? '0' : '100%'});
    margin: 0;
    padding 0;

    ${media.phone`width: 100vw;`}
    ${media.tablet`width: 80vw;`}
`

const Hamburger = styled.img`
    width: 4rem;
    height: 4rem;
    transform: translateX(-120%) translateY(20%);
    cursor: pointer;
`

const CloseNavigation = styled.img`
    width: 2.5rem;
    height: 2.5rem;
    margin-right: 1.2rem;
    cursor: pointer;
    transform: translateY(-110%);
`

const NavigationList = styled.ul`
    list-style-type: none;
    text-align: center;
    padding: 0;
`

const NavigationListItem = styled.li`
    margin: 2rem;
    font-size: 2.5rem;
    cursor: pointer;
    
    &:after{
        content: '';
        display: block;
        width: 10%;
        height: .2rem;
        background-color: white;
        margin: 1rem auto;
        transition: .1s transform linear;
    }

    &:hover{
        &:after{
            transform: scaleX(2);
        }
    }
`

const UserProfileContainer = styled.div`
    display: flex;
    padding: 3rem;
    justify-content: space-between;
    align-items: center;
    background-color: #171f23;
`

const UserProfileAvatar = styled.img`
    width: 7rem;
    height: 7rem;
    background-color: pink;
`

const UserProfileUsername = styled.a`
    text-decoration: none;
    color: white;
    font-size: 2rem;
`

interface NavMenuComponentState {
    navigationOpen: boolean
}

class NavMenu extends React.Component<UserStore.UserState & typeof UserStore.actionCreators, NavMenuComponentState> {

    constructor() {
        super()
        this.state = {
            navigationOpen: false,
        }
    }

    private changeNavigationState = () => {
        this.setState(prevState => {
            return { navigationOpen: !prevState.navigationOpen }
        })
    }

    private logout = () => {
        setAuthorizationHeader()
        localStorage.removeItem('iotDataStoreJWT')
        this.props.logoutUser()
    }

    public render() {
        const isAuthenticated = !!this.props.username
        return (
            <Navigation open={this.state.navigationOpen}>
                <Hamburger src={HamburgerSvg} alt="open nav" onClick={() => this.changeNavigationState()} />
                <MoveToTheRight>
                    <CloseNavigation src={CloseNavigationSvg} onClick={() => this.changeNavigationState()} />
                </MoveToTheRight>
                <NavigationList className="navigation-list">
                    {isAuthenticated &&
                        <li>
                            <UserProfileContainer>
                            <UserProfileAvatar src="http://www.fizykon.org/images/Pusty_obraz_15x10_300dpi.jpg"/>
                                <UserProfileUsername href="/">{this.props.username}</UserProfileUsername>
                            </UserProfileContainer>
                        </li>
                    }
                    <NavigationListItem><Link to="/">Home</Link></NavigationListItem>
                    {!isAuthenticated &&
                        <div>
                            <NavigationListItem><Link to="/login">Login</Link></NavigationListItem>
                            <NavigationListItem><Link to="/register">Register</Link></NavigationListItem>
                        </div>
                    }
                    {isAuthenticated &&
                        <div>
                            <NavigationListItem><Link to="/createproject">Create Project</Link></NavigationListItem>
                            <NavigationListItem><Link to="/myprojects">My projects</Link></NavigationListItem>
                            <NavigationListItem onClick={this.logout}>Logout</NavigationListItem>
                        </div>
                    }
                </NavigationList>
            </Navigation>
        )
    }
}

const mapStateToProps = (state: ApplicationState) => state.user

export default connect(mapStateToProps, UserStore.actionCreators)(NavMenu)