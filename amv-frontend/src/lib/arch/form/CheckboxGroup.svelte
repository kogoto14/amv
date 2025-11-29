<script lang="ts">
  import { ValidationMessage } from '@felte/reporter-svelte';

  interface Props {
    id: string;
    label: string;
    options: { value?: string; label: string }[] | string[];
    checkedValues: string[];
    disabled?: boolean;
  }

  let { id, label, options, checkedValues = $bindable(), disabled = false }: Props = $props();
</script>

<ValidationMessage for={id} let:messages>
  <fieldset>
    {#each options as option}
      {@const value = typeof option === 'string' ? option : (option.value ?? option.label)}
      {@const label = typeof option === 'string' ? option : option.label}

      <input
        id={`${id}-${value}`}
        name={`${id}-${value}`}
        type="checkbox"
        {value}
        bind:group={checkedValues}
        aria-describedby={`invalid-${id}`}
        {disabled}
      />
      <label for={`${id}-${value}`}>{label}</label>
    {/each}
    <small id="invalid-{id}">{messages || ''}</small>
  </fieldset>
</ValidationMessage>
