import { BookOpen, Library, BookMarked } from 'lucide-react'

function App() {
  return (
    <main className="min-h-screen bg-stone-100 flex items-center justify-center p-8">
      <div className="max-w-lg w-full rounded-2xl bg-white p-10 shadow-lg">
        <div className="flex items-center gap-3 mb-6">
          <BookOpen size={36} className="text-emerald-700" />

          <h1 className="text-3xl font-bold text-stone-800">
            SpringBook
          </h1>
        </div>

        <p className="text-stone-600 mb-8">
          Your personal reading space.
        </p>

        <div className="flex gap-4">
          <Library className="text-emerald-700" />
          <span className="text-stone-700">
            Your personal library
          </span>
        </div>

        <div className="flex gap-4 mt-4">
          <BookMarked className="text-emerald-700" />
          <span className="text-stone-700">
            Your reading journey
          </span>
        </div>
      </div>
    </main>
  )
}

export default App