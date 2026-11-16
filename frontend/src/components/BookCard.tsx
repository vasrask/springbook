import { BookOpen } from 'lucide-react'
import type { UserBook } from '../types/book'

interface BookCardProps {
  userBook: UserBook
}

const statusLabels: Record<UserBook['status'], string> = {
  WANT_TO_READ: 'Want to read',
  CURRENTLY_READING: 'Currently reading',
  READ: 'Read',
  ON_HOLD: 'On hold',
  DROPPED: 'Dropped',
}

export default function BookCard({ userBook }: BookCardProps) {
  return (
    <article className="overflow-hidden rounded-xl border border-stone-200 bg-white shadow-sm">
      <div className="flex h-44 items-center justify-center bg-emerald-50">
        <BookOpen size={48} className="text-emerald-800" />
      </div>

      <div className="p-4">
        <h3 className="line-clamp-2 font-semibold text-stone-900">
          {userBook.bookTitle}
        </h3>

        <p className="mt-1 text-sm text-stone-500">
          {userBook.bookAuthor}
        </p>

        <div className="mt-4">
          <span className="inline-block rounded-full bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-800">
            {statusLabels[userBook.status]}
          </span>
        </div>

        {userBook.rating !== null && (
          <p className="mt-3 text-sm text-amber-600">
            {'★'.repeat(userBook.rating)}
            {'☆'.repeat(5 - userBook.rating)}
          </p>
        )}
      </div>
    </article>
  )
}