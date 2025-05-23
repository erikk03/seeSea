import React from 'react';
import { Card, CardBody } from '@heroui/react';
import loginImage from '../../assets/login/login_ships.png';
import LoginForm from '../../components/LoginForm';

export default function Login({ onLogin }) {
  return (
    <div className="w-screen h-screen flex flex-row overflow-hidden">
      <div className="flex-1 bg-[#003C62] flex flex-col items-center justify-center px-4 py-6">
        <h1 className="text-white text-2xl font-semibold mb-6 text-center">
          Sign in for <span className="text-yellow-400">premium</span> access!
        </h1>
        <Card className="max-w-md w-full border-none shadow-lg">
          <CardBody className="p-6">
            <LoginForm onLogin={onLogin} />
          </CardBody>
        </Card>
        <p className="text-white mt-6 text-sm text-center">
          New here?{" "}
          <a href="/register" className="text-blue-300 underline ml-1">
            Create an account
          </a>
        </p>
      </div>
      <div className="h-full w-auto">
        <img
          src={loginImage}
          alt="Login visual"
          className="h-full w-auto object-cover"
        />
      </div>
    </div>
  );
}
