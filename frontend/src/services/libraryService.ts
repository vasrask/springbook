import api from '../lib/api'
import type {
  UserBook,
  UserBookRequest,
} from '../types/book'

export const getLibrary = async (): Promise<UserBook[]> => {
  const response = await api.get<UserBook[]>('/library')
  return response.data
}

export const getLibraryByStatus = async (
  status: string,
): Promise<UserBook[]> => {
  const response = await api.get<UserBook[]>(
    `/library/status/${status}`,
  )

  return response.data
}

export const addToLibrary = async (
  data: UserBookRequest,
): Promise<UserBook> => {
  const response = await api.post<UserBook>('/library', data)
  return response.data
}