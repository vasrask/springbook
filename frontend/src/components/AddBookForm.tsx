import { useState, type FormEvent } from 'react'
import axios from 'axios'
import { X } from 'lucide-react'

import { createBook } from '../services/bookService'
import { addToLibrary } from '../services/libraryService'
import type { ReadingStatus } from '../types/book'

interface AddBookFormProps {
  onClose: () => void
  onAdded: () => void
}

export default function AddBookForm({
  onClose,
  onAdded,
}: AddBookFormProps) {
  const [title, setTitle] = useState('')
  const [author, setAuthor] = useState('')
  const [isbn, setIsbn] = useState('')
  const [genre, setGenre] = useState('')
  const [description, setDescription] = useState('')
  const [coverUrl, setCoverUrl] = useState('')
  const [publicationYear, setPublicationYear] = useState('')
  const [status, setStatus] = useState<ReadingStatus>('WANT_TO_READ')

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError('')
    setLoading(true)

    try {
      // First, create the book in the shared catalog.
      const book = await createBook({
        title: title.trim(),
        author: author.trim(),
        isbn: isbn.trim() || null,
        genre: genre.trim() || null,
        description: description.trim() || null,
        coverUrl: coverUrl.trim() || null,
        publicationYear: publicationYear
          ? Number(publicationYear)
          : null,
      })

      // Then add the catalog book to this user's library.
      await addToLibrary({
        bookId: book.id,
        status,
      })

      onAdded()
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const message = err.response?.data?.message

        setError(
          typeof message === 'string'
            ? message
            : err.response?.status === 409
              ? 'This book may already exist in the catalog or your library.'
              : 'Unable to add this book. Please check the details and try again.',
        )
      } else {
        setError('Something went wrong. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/50 p-4">
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="add-book-title"
        className="my-auto w-full max-w-xl rounded-2xl bg-white p-6 shadow-xl"
      >
        <div className="mb-6 flex items-center justify-between">
          <div>
            <h2
              id="add-book-title"
              className="text-2xl font-bold text-stone-900"
            >
              Add a book
            </h2>
            <p className="mt-1 text-sm text-stone-500">
              Add a book to your personal library.
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            aria-label="Close form"
            className="rounded-lg p-2 text-stone-500 hover:bg-stone-100"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="book-title"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Title *
            </label>
            <input
              id="book-title"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              required
              maxLength={255}
              className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="Book title"
            />
          </div>

          <div>
            <label
              htmlFor="book-author"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Author *
            </label>
            <input
              id="book-author"
              value={author}
              onChange={(event) => setAuthor(event.target.value)}
              required
              maxLength={255}
              className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="Author name"
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="book-isbn"
                className="mb-1 block text-sm font-medium text-stone-700"
              >
                ISBN
              </label>
              <input
                id="book-isbn"
                value={isbn}
                onChange={(event) => setIsbn(event.target.value)}
                maxLength={20}
                className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
                placeholder="Optional"
              />
            </div>

            <div>
              <label
                htmlFor="book-genre"
                className="mb-1 block text-sm font-medium text-stone-700"
              >
                Genre
              </label>
              <input
                id="book-genre"
                value={genre}
                onChange={(event) => setGenre(event.target.value)}
                maxLength={100}
                className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
                placeholder="e.g. Fantasy"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="book-year"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Publication year
            </label>
            <input
              id="book-year"
              type="number"
              min="1"
              max="2100"
              value={publicationYear}
              onChange={(event) => setPublicationYear(event.target.value)}
              className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="e.g. 2020"
            />
          </div>

          <div>
            <label
              htmlFor="book-cover"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Cover image URL
            </label>
            <input
              id="book-cover"
              type="url"
              value={coverUrl}
              onChange={(event) => setCoverUrl(event.target.value)}
              className="w-full rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="https://..."
            />
          </div>

          <div>
            <label
              htmlFor="book-description"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Description
            </label>
            <textarea
              id="book-description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={3}
              className="w-full resize-y rounded-lg border border-stone-300 px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
              placeholder="A short description..."
            />
          </div>

          <div>
            <label
              htmlFor="book-status"
              className="mb-1 block text-sm font-medium text-stone-700"
            >
              Reading status
            </label>
            <select
              id="book-status"
              value={status}
              onChange={(event) =>
                setStatus(event.target.value as ReadingStatus)
              }
              className="w-full rounded-lg border border-stone-300 bg-white px-3 py-2 outline-none focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
            >
              <option value="WANT_TO_READ">Want to read</option>
              <option value="CURRENTLY_READING">Currently reading</option>
              <option value="READ">Read</option>
              <option value="ON_HOLD">On hold</option>
              <option value="DROPPED">Dropped</option>
            </select>
          </div>

          {error && (
            <p
              role="alert"
              className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700"
            >
              {error}
            </p>
          )}

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="rounded-lg border border-stone-300 px-4 py-2 text-sm font-medium text-stone-700 hover:bg-stone-50 disabled:opacity-50"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={loading}
              className="rounded-lg bg-emerald-800 px-5 py-2 text-sm font-semibold text-white hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? 'Adding...' : 'Add to library'}
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}