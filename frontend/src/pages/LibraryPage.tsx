import { useCallback, useEffect, useState } from 'react'
import { BookOpen, LogOut, Plus, RefreshCw } from 'lucide-react'
import axios from 'axios'

import { useAuth } from '../context/AuthContext'
import { getLibrary } from '../services/libraryService'
import BookCard from '../components/BookCard'
import AddBookForm from '../components/AddBookForm'
import type { UserBook } from '../types/book'

export default function LibraryPage() {
  const { user, logout } = useAuth()

  const [books, setBooks] = useState<UserBook[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showAddBook, setShowAddBook] = useState(false)

  const loadLibrary = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const result = await getLibrary()
      setBooks(result)
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 401) {
        logout()
        return
      }

      setError('Unable to load your library. Please try again.')
    } finally {
      setLoading(false)
    }
  }, [logout])

  useEffect(() => {
    void loadLibrary()
  }, [loadLibrary])

  const handleBookAdded = async () => {
    setShowAddBook(false)
    await loadLibrary()
  }

  return (
    <main className="min-h-screen bg-stone-100">
      <header className="border-b border-stone-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <div className="flex items-center gap-3">
            <BookOpen className="text-emerald-800" size={28} />
            <span className="text-xl font-bold text-stone-900">
              SpringBook
            </span>
          </div>

          <button
            onClick={logout}
            className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-stone-600 hover:bg-stone-100"
          >
            <LogOut size={17} />
            Log out
          </button>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 py-12">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold text-stone-900">
              My Library
            </h1>

            <p className="mt-2 text-stone-600">
              Welcome back, {user?.displayName}.
            </p>
          </div>

          <button
            onClick={() => setShowAddBook(true)}
            className="flex items-center gap-2 rounded-lg bg-emerald-800 px-4 py-3 font-semibold text-white transition hover:bg-emerald-900"
          >
            <Plus size={18} />
            Add a book
          </button>
        </div>

        <div className="mt-10">
          {loading ? (
            <p className="py-12 text-center text-stone-500">
              Loading your library...
            </p>
          ) : error ? (
            <div className="rounded-xl bg-red-50 p-6 text-center text-red-700">
              <p>{error}</p>

              <button
                onClick={() => void loadLibrary()}
                className="mt-4 inline-flex items-center gap-2 font-semibold"
              >
                <RefreshCw size={16} />
                Try again
              </button>
            </div>
          ) : books.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-stone-300 bg-white px-6 py-16 text-center">
              <BookOpen
                className="mx-auto text-emerald-800"
                size={44}
              />

              <h2 className="mt-4 text-xl font-semibold text-stone-800">
                Your library is empty
              </h2>

              <p className="mt-2 text-stone-500">
                Add your first book to start building your collection.
              </p>

              <button
                onClick={() => setShowAddBook(true)}
                className="mt-6 rounded-lg bg-emerald-800 px-5 py-3 font-semibold text-white hover:bg-emerald-900"
              >
                Add your first book
              </button>
            </div>
          ) : (
            <>
              <p className="mb-5 text-sm text-stone-500">
                {books.length} {books.length === 1 ? 'book' : 'books'}
              </p>

              <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {books.map((book) => (
                  <BookCard key={book.id} userBook={book} />
                ))}
              </div>
            </>
          )}
        </div>
      </section>

      {showAddBook && (
        <AddBookForm
          onClose={() => setShowAddBook(false)}
          onAdded={handleBookAdded}
        />
      )}
    </main>
  )
}