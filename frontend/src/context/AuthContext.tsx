import {
  createContext,
  useContext,
  useState,
  type ReactNode,
} from 'react'

import {
  getStoredUser,
  login as loginRequest,
  logout as logoutRequest,
  register as registerRequest,
} from '../services/authService'

import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from '../types/auth'

type AuthUser = Omit<AuthResponse, 'token'>

interface AuthContextType {
  user: AuthUser | null
  isAuthenticated: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => getStoredUser())

  const login = async (data: LoginRequest): Promise<void> => {
    const response = await loginRequest(data)

    setUser({
      id: response.id,
      email: response.email,
      displayName: response.displayName,
    })
  }

  const register = async (data: RegisterRequest): Promise<void> => {
    const response = await registerRequest(data)

    setUser({
      id: response.id,
      email: response.email,
      displayName: response.displayName,
    })
  }

  const logout = (): void => {
    logoutRequest()
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: user !== null,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}