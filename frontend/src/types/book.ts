export type ReadingStatus =
  | 'WANT_TO_READ'
  | 'CURRENTLY_READING'
  | 'READ'
  | 'ON_HOLD'
  | 'DROPPED'

export interface UserBook {
  id: number
  bookId: number
  bookTitle: string
  bookAuthor: string
  status: ReadingStatus
  rating: number | null
  startedAt: string | null
  finishedAt: string | null
  personalSummary: string | null
  personalReview: string | null
}

export interface UserBookRequest {
  bookId: number
  status: ReadingStatus
  rating?: number | null
  startedAt?: string | null
  finishedAt?: string | null
  personalSummary?: string | null
  personalReview?: string | null
}

export interface BookRequest {
  isbn?: string | null
  title: string
  author: string
  description?: string | null
  coverUrl?: string | null
  genre?: string | null
  publicationYear?: number | null
}

export interface BookResponse {
  id: number
  isbn: string | null
  title: string
  author: string
  description: string | null
  coverUrl: string | null
  genre: string | null
  publicationYear: number | null
}