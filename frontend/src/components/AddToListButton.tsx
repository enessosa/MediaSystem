import { CheckIcon, PlusIcon } from '@primer/octicons-react'
import { Button } from '@primer/react'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import { ApiError } from '../api/client'
import { addToList, type MediaSearchResult } from '../api/media'
import { useI18n } from '../i18n/useI18n'

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
  const { t } = useI18n()

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
        {t.media.inYourList}
      </Button>
    )
  }

  if (state === 'exists') {
    return (
      <Button size={size} leadingVisual={CheckIcon} disabled>
        {t.media.alreadyInList}
      </Button>
    )
  }

  return (
    <Button size={size} leadingVisual={PlusIcon} loading={state === 'saving'} onClick={add}>
      {state === 'error' ? t.media.addFailed : t.media.addToList}
    </Button>
  )
}

export default AddToListButton
