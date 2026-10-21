import { CheckIcon, PlusIcon } from '@primer/octicons-react'
import { Button } from '@primer/react'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import { ApiError } from '../api/client'
import { addToList, type MediaSearchResult } from '../api/media'

type AddState = 'idle' | 'saving' | 'exists' | 'error'

interface AddToListButtonProps {
  item: MediaSearchResult
  /** ID des Listeneintrags, falls das Medium schon in der Liste ist – dann führt der Button dorthin. */
  entryId?: number
  size?: 'small' | 'medium'
}

function AddToListButton({ item, entryId, size }: AddToListButtonProps) {
  const [state, setState] = useState<AddState>('idle')
  const [addedId, setAddedId] = useState<number | null>(null)
  const navigate = useNavigate()

  async function add() {
    setState('saving')
    try {
      const entry = await addToList(item)
      setAddedId(entry.id)
    } catch (err) {
      setState(err instanceof ApiError && err.status === 409 ? 'exists' : 'error')
    }
  }

  const listedId = addedId ?? entryId
  if (listedId !== undefined) {
    return (
      <Button size={size} leadingVisual={CheckIcon} onClick={() => navigate(`/entries/${listedId}`)}>
        In deiner Liste
      </Button>
    )
  }

  if (state === 'exists') {
    return (
      <Button size={size} leadingVisual={CheckIcon} disabled>
        Schon in deiner Liste
      </Button>
    )
  }

  return (
    <Button size={size} leadingVisual={PlusIcon} loading={state === 'saving'} onClick={add}>
      {state === 'error' ? 'Fehler – erneut versuchen' : 'Zur Liste'}
    </Button>
  )
}

export default AddToListButton
