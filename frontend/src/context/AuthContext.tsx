import { createContext, useContext, useState, type ReactNode } from 'react'
import { authApi } from '../api/authApi'
import type { LoginRequest, RegisterRequest } from '../types'

interface AuthContextValue {
  token: string | null
  isAuthenticated: boolean
  email: string
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem('token'),
  )

  const storeToken = (accessToken: string) => {
    localStorage.setItem('token', accessToken)
    setToken(accessToken)
  }

  const login = async (data: LoginRequest) => {
    const res = await authApi.login(data)
    storeToken(res.accessToken)
  }

  const register = async (data: RegisterRequest) => {
    const res = await authApi.register(data)
    storeToken(res.accessToken)
  }

  const logout = () => {
    localStorage.removeItem('token')
    setToken(null)
  }

  const email = token ? decodeEmail(token) : ''

  return (
    <AuthContext.Provider
      value={{ token, isAuthenticated: !!token, email, login, register, logout }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

function decodeEmail(token: string): string {
  try {
    const payload = token.split('.')[1]
    return JSON.parse(atob(payload)).sub ?? ''
  } catch {
    return ''
  }
}
