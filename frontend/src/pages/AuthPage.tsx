import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import axios from 'axios'
import { BookOpen } from 'lucide-react'

import { useAuth } from '../context/AuthContext'

interface AuthPageProps {
  mode: 'login' | 'register'
}

export default function AuthPage({ mode }: AuthPageProps) {
  const isRegister = mode === 'register'

  const { login, register } = useAuth()
  const navigate = useNavigate()

  const [displayName, setDisplayName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError('')
    setLoading(true)

    try {
      if (isRegister) {
        await register({ displayName, email, password })
      } else {
        await login({ email, password })
      }

      navigate('/library', { replace: true })
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const message = err.response?.data?.message

        setError(
          typeof message === 'string'
            ? message
            : 'Authentication failed. Please check your details and try again.',
        )
      } else {
        setError('Something went wrong. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-stone-100 px-4 py-10">
      <section className="w-full max-w-md rounded-2xl bg-white p-8 shadow-sm">
        <div className="mb-8 text-center">
          <div className="mb-4 flex justify-center">
            <div className="rounded-xl bg-emerald-100 p-3 text-emerald-800">
              <BookOpen size={30} />
            </div>
          </div>

          <h1 className="text-3xl font-bold text-stone-900">
            SpringBook
          </h1>

          <p className="mt-2 text-sm text-stone-500">
            {isRegister
              ? 'Create your personal reading space.'
              : 'Welcome back to your reading space.'}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          {isRegister && (
            <div>
              <label
                htmlFor="displayName"
                className="mb-1 block text-sm font-medium text-stone-700"
              >
                Display name
              </label>

              <input
                id="displayName"
                type="text"
                value={displayName}
                onChange={(event) => setDisplayName(event.target.value)}
                required
                maxLength={100}
                autoComplete="name"
                className="w-full rounded-lg border border-stone-300 px-4 py-3 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
                placeholder="Your name"
              />
            </div>
          )}

          <div>
            <label
              htmlFor="email"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Email
            </label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
              autoComplete="email"
              className="w-full rounded-lg border border-stone-300 px-4 py-3 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Password
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              minLength={8}
              autoComplete={isRegister ? 'new-password' : 'current-password'}
              className="w-full rounded-lg border border-stone-300 px-4 py-3 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="At least 8 characters"
            />
          </div>

          {error && (
            <p
              role="alert"
              className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700"
            >
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-emerald-800 px-4 py-3 font-semibold text-white transition hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading
              ? 'Please wait...'
              : isRegister
                ? 'Create account'
                : 'Log in'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-stone-600">
          {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
          <Link
            to={isRegister ? '/login' : '/register'}
            className="font-semibold text-emerald-800 hover:underline"
          >
            {isRegister ? 'Log in' : 'Register'}
          </Link>
        </p>
      </section>
    </main>
  )
}