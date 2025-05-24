import React, { useEffect, useState } from 'react';
import { Card, CardBody } from '@heroui/react';
import LoginForm from '../../components/LoginForm';

import img1 from '../../assets/authPages/ship1.jpg';
import img2 from '../../assets/authPages/ship2.jpg';
import img3 from '../../assets/authPages/ship3.jpg';
import img4 from '../../assets/authPages/ship4.jpg';
import img5 from '../../assets/authPages/ship5.jpg';
import img6 from '../../assets/authPages/ship6.jpg';
import img7 from '../../assets/authPages/ship7.jpg';

const images = [img1, img2, img3, img4, img5, img6, img7];

export default function Login({ onLogin }) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImageIndex((prevIndex) =>
        (prevIndex + 1) % images.length
      );
    }, 5000); // Change every 5 seconds

    return () => clearInterval(interval);
  }, []);

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

      <div className="h-full w-auto transition-opacity duration-1000 ease-in-out">
        <img
          src={images[currentImageIndex]}
          alt="Login visual"
          className="h-full w-auto object-cover"
        />
      </div>
    </div>
  );
}
