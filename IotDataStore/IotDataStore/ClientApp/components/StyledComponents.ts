import styled, { css } from 'styled-components'

interface SizesInterface{
    [index:string]: number
};

const sizes: SizesInterface = {
    tablet: 960,
    phone: 420
}

// Iterate through the sizes and create a media template
export const media = Object.keys(sizes).reduce((acc: any, label: string) => {
    acc[label] = (...args: any[]) => css`
        @media (max-width: ${sizes[label]}px) {
        ${css.call(undefined, ...args)}
    }`   
    return acc
}, {})

export const MoveToTheRight = styled.div`
    display: flex;
    justify-content: flex-end;
`

export const MoveToTheMiddle = styled.div`
    display: flex;
    width: 100vw;
    height: 100vh;
    justify-content: center;
    align-items: center;
`

export const InputContainer = styled.div`
    border: 0.2rem solid white;
    border-radius: .5rem;
    margin-top: 1.5rem;
    margin-bottom: 1.5rem;
    padding: 0;
    display: flex;
`

export const InputLabel = styled.label`
    font-size: 2rem;
    text-transform: lowercase;
    height: 100%;
    width: 40%;
    background-color: white;
    padding: 1rem;

    ${media.phone`
        font-size: 1.5rem;
    `}
`

export const InputTextField = styled.input`
    background-color: #22333B;
    border: none;
    font-size: 2rem;
    color: white;
    text-align: right;
    width: 100%;

    ${media.phone`
        font-size: 1.5rem;
    `}
`

export const SubmitButton = styled.input`
    font-size; 2.5rem;
    background-color: #22333B;
    border: 0;
    border: 0.2rem solid white;
    border-radius: .5rem;
    color: white;
    padding: 1rem;
`

export const Form = styled.form`
    background-color: #22333B;
    padding: 3rem;
    border: .2rem solid #f0f0f0;
    border-radius: 1rem;
    width: 100%;
    box-sizing: border-box;
    
    ${media.phone`
        padding: 1rem;
    `}
`

export const Container = styled.div`
    width: 900px;
    margin: 0 auto;
    
    ${media.tablet`
        width: 80%;
        margin: 0 auto;
    `}

    ${media.phone`
        width: 95%;
        margin: 0 auto;
    `}
`

export const InputTextAreaField = styled.textarea`
    border: 0.2rem solid white;
    border-radius: .5rem;
    color: white;
    background-color: #22333B;
    margin: 1.5rem 0;
    width: 100%;
    height: 20rem;
    box-sizing: border-box;
`

export const MoveToTheHorizontalMiddle = styled.div`
    display: flex;
    justify-content: center;
`

interface LoadingProps {
    color: string
}

export const Loading = styled.div`
    margin: 3rem;
    display: inline-block;
    width: 64px;
    height: 64px;

    &:after{
        content: " ";
        display: block;
        width: 46px;
        height: 46px;
        margin: 1px;
        border-radius: 50%;
        border: 5px solid ${(props: LoadingProps) => props.color};
        border-color: ${(props: LoadingProps) => props.color} transparent ${(props: LoadingProps) => props.color} transparent;
        animation: lds-dual-ring 1.2s linear infinite;        
    }

    @keyframes lds-dual-ring {
        0% {
            transform: rotate(0deg);
        }
        100% {
            transform: rotate(360deg);
        }
    }
`

interface ButtonProps {
    color: string
}

export const Button = styled.div`
    background-color: ${(props: ButtonProps) => props.color};
    font-size: 1.5rem;
    font-weight: bold;
    color: white;
    height: 2rem;
    display: flex;
    align-items: center;
    padding: 1rem;
`

export const SelectField = styled.select`
    background-color: #22333B;
    width: 100%;
    color: white;
    font-size: 2rem;
`

export const SubPageContainer = styled.div`
    background-color: #f0f0f0;
    border-radius: .2rem;
    padding: 3rem;
    margin: 1.5rem auto;
    box-sizing: border-box;
`

export const SubPageHeader = styled.header`
    font-size: 3rem;
`

export const SubPageDescription = styled.p`
    font-size: 1.5rem;
`

export const SubPageNavigationContainer = styled.nav`
    width: 100%;
`

export const SubPageNavigationList = styled.ul`
    list-style-type: none;
    padding: 0;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
`

interface SubPageNavigationListItemProps {
    active: boolean;
}

export const SubPageNavigationListItem = styled.li`
    font-size: 1.5rem;
    margin: 0 1.5rem;
    border: .2rem dashed lightblue;
    padding: 1rem;
    background-color: ${(props: SubPageNavigationListItemProps) => props.active ? 'white' : 'lightblue'};

    &:first-child{
        margin-left: 0;
    }
`