import { BookOpen, LogOut } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export default function LibraryPage() {
  const { user, logout } = useAuth()

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
        <h1 className="text-3xl font-bold text-stone-900">
          Welcome, {user?.displayName}!
        </h1>

        <p className="mt-2 text-stone-600">
          Your personal reading space starts here.
        </p>

        <div className="mt-10 rounded-2xl border border-dashed border-stone-300 bg-white p-12 text-center">
          <BookOpen className="mx-auto text-emerald-800" size={42} />

          <h2 className="mt-4 text-xl font-semibold text-stone-800">
            Your library is waiting
          </h2>

          <p className="mt-2 text-stone-500">
            We'll connect your books and reading progress here next.
          </p>
        </div>
      </section>
    </main>
  )
}