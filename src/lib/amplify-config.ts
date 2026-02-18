import { Amplify } from 'aws-amplify';

const userPoolId = process.env.NEXT_PUBLIC_COGNITO_USER_POOL_ID;
const userPoolClientId = process.env.NEXT_PUBLIC_COGNITO_CLIENT_ID;
const region = process.env.NEXT_PUBLIC_REGION || 'af-south-1';

export const isCognitoConfigured = !!(userPoolId && userPoolClientId);

export function configureAmplify() {
  if (!isCognitoConfigured) {
    return;
  }

  Amplify.configure({
    Auth: {
      Cognito: {
        userPoolId: userPoolId!,
        userPoolClientId: userPoolClientId!,
        loginWith: {
          email: true,
        },
      },
    },
  });
}
