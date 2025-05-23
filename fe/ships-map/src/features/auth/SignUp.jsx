// pages/SignupPage.jsx
import React from 'react';
import { Card, CardBody } from '@heroui/react';
import SignupForm from '../../components/SignUpForm';
import signupImage from '../../assets/signup/signup.png';

export default function SignupPage({ onLogin }) {
  return (
    <div className="w-screen h-screen flex flex-row overflow-hidden">
      {/* Left Side: Image */}
      <div className="h-full w-auto">
        <img
          src={signupImage}
          alt="Signup visual"
          className="h-full w-auto object-cover"
        />
      </div>

      {/* Right Side: Blue background + Form */}
      <div className="flex-1 bg-[#003C62] flex flex-col items-center justify-center px-4 py-6 relative">
        <h1 className="text-white text-2xl font-semibold mb-6 text-center">
          Sign up for <span className="text-yellow-400">premium</span> access!
        </h1>

        <Card className="max-w-md w-full border-none shadow-lg">
          <CardBody className="p-6">
            <SignupForm onLogin={onLogin} />
          </CardBody>
        </Card>

        <p className="text-white mt-6 text-sm text-center">
          Already registered?{' '}
          <a href="/signin" className="text-blue-300 underline ml-1">
            Sign in here
          </a>
        </p>
      </div>
    </div>
  );
}
