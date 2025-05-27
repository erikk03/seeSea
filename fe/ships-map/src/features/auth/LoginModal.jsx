import {
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
} from "@heroui/react";
import { useState } from "react";
import LoginForm from "../../components/LoginForm";
import SignUpForm from "../../components/SignUpForm";

export default function LoginModal({ isOpen, onClose, onLogin }) {
  const [mode, setMode] = useState("signin");

  const handleSuccess = (token) => {
    onLogin(token);
    onClose();
    setMode("signin"); // reset mode next time
  };

  return (
    <Modal isOpen={isOpen} onOpenChange={onClose} backdrop="blur" className="z-[1200]">
      <ModalContent>
        {(onCloseModal) => (
          <>
            <ModalHeader className="text-lg font-semibold text-center flex items-center justify-center">
              {mode === "signin" ? "Sign In for premium access!" : "Create an Account!"}
            </ModalHeader>
            <ModalBody className="pb-6">
              {mode === "signin" ? (
                <>
                  <LoginForm
                    onLogin={(token) => {
                      handleSuccess(token);
                      onCloseModal();
                    }}
                  />
                  <p className="mt-4 text-sm text-center">
                    Don’t have an account?{" "}
                    <button
                      onClick={() => setMode("signup")}
                      className="text-blue-600 hover:underline"
                    >
                      Create one
                    </button>
                  </p>
                </>
              ) : (
                <>
                  <SignUpForm
                    onLogin={(token) => {
                      handleSuccess(token);
                      onCloseModal();
                    }}
                  />
                  <p className="mt-4 text-sm text-center">
                    Already have an account?{" "}
                    <button
                      onClick={() => setMode("signin")}
                      className="text-blue-600 hover:underline"
                    >
                      Sign in
                    </button>
                  </p>
                </>
              )}
            </ModalBody>
          </>
        )}
      </ModalContent>
    </Modal>
  );
}
