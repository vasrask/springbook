import api from '../lib/api'
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from '../types/auth'

const TOKEN_KEY = 'springbook_token'
const USER_KEY = 'springbook_user'

const saveSession = (data: AuthResponse): void => {
  localStorage.setItem(TOKEN_KEY, data.token)

  localStorage.setItem(
    USER_KEY,
    JSON.stringify({
      id: data.id,
      email: data.email,
      displayName: data.displayName,
    }),
  )
}

export const register = async (
  data: RegisterRequest,
): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/register', data)
  saveSession(response.data)
  return response.data
}

export const login = async (
  data: LoginRequest,
): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', data)
  saveSession(response.data)
  return response.data
}

export const logout = (): void => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export const getStoredToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY)
}

export const getStoredUser = (): Omit<AuthResponse, 'token'> | null => {
  const storedUser = localStorage.getItem(USER_KEY)

  if (!storedUser) {
    return null
  }

  try {
    return JSON.parse(storedUser) as Omit<AuthResponse, 'token'>
  } catch {
    logout()
    return null
  }
}

export const isAuthenticated = (): boolean => {
  return getStoredToken() !== null
}