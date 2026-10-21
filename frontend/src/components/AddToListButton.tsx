import { CheckIcon, PlusIcon } from '@primer/octicons-react'
import { Button } from '@primer/react'
import { useState } from 'react'
import { ApiError } from '../api/client'
import { addToList, type MediaSearchResult } from '../api/media'

type AddState = 'idle' | 'saving' | 'added' | 'exists' | 'error'

function AddToListButton({ item, size }: { item: MediaSearchResult; size?: 'small' | 'medium' }) {
  const [state, setState] = useState<AddState>('idle')

  async function add() {
    setState('saving')
    try {
      await addToList(item)
      setState('added')
    } catch (err) {
      setState(err instanceof ApiError && err.status === 409 ? 'exists' : 'error')
    }
  }

  if (state === 'added' || state === 'exists') {
    return (
      <Button size={size} leadingVisual={CheckIcon} disabled>
        {state === 'added' ? 'Hinzugefügt' : 'Schon in deiner Liste'}
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
