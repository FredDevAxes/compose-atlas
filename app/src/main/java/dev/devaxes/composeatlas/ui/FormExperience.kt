package dev.devaxes.composeatlas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.validateSession

@Composable
internal fun FormExperience(onExit: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var topic by rememberSaveable { mutableStateOf(0) }
    var notes by rememberSaveable { mutableStateOf(false) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var confirmed by rememberSaveable { mutableStateOf(false) }
    val errors = validateSession(name, email)
    val emailFocus = remember { FocusRequester() }
    val focus = LocalFocusManager.current
    val topics = listOf("Motion", "Layouts", "Input")
    val submit = {
        attempted = true
        if (errors.valid) {
            focus.clearFocus()
            confirmed = true
        }
    }
    ExperimentFrame(
        "Session builder",
        "Local form / nothing is sent",
        onExit,
        explanation = {
            Lesson(
                "State, then validation",
                "Text fields write saveable state. A pure validation function derives errors. Submit reveals them; correcting the input clears them immediately. No network request or email delivery occurs.",
            )
            Lesson(
                "Follow the keyboard",
                "Name uses the Next IME action and a FocusRequester to move to email. Email requests an email keyboard; Done validates. The page scrolls above the keyboard using imePadding.",
            )
            Lesson(
                "Accessible feedback",
                "Material fields expose their labels and error state. Supporting text explains how to correct each value. The confirmation repeats the submitted choices instead of relying on a transient message.",
            )
            Lesson(
                "Try it",
                "Submit the empty form, enter an invalid address, then load the example. Change a topic and include the notes preview. Recreate the activity to check saved input and confirmation.",
            )
        },
    ) {
        AnimatedContent(targetState = confirmed, label = "Form confirmation") { complete ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (complete) {
                    LabSurface {
                        Text("Session prepared", style = MaterialTheme.typography.headlineMedium, color = Instrument.Lime)
                        Text(name.trim(), style = MaterialTheme.typography.titleLarge)
                        Text(email.trim(), modifier = Modifier.testTag("confirmed-email"))
                        Text("Topic: ${topics[topic]}")
                        if (notes) {
                            Text("Notes preview", style = MaterialTheme.typography.titleMedium)
                            Text(
                                when (topic) {
                                    0 -> "Compare duration and easing with a spring’s damping and stiffness."
                                    1 -> "Trace how constraints determine measurement and placement."
                                    else -> "Test focus order, keyboard actions and accessible error feedback."
                                },
                                color = Instrument.Muted,
                            )
                        } else {
                            Text("No notes preview.")
                        }
                        Text("This is a local preview. No booking or email was created.", color = Instrument.Muted)
                        Button(onClick = { confirmed = false }, modifier = Modifier.fillMaxWidth()) { Text("Edit session") }
                    }
                } else {
                    Text("Make room to learn.", style = MaterialTheme.typography.headlineMedium)
                    Text("Use fictional details to try focus, validation and saved state.", color = Instrument.Muted)
                    OutlinedTextField(
                        name,
                        { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        isError = attempted && errors.name != null,
                        supportingText = { if (attempted && errors.name != null) Text(errors.name) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() }),
                        modifier = Modifier.fillMaxWidth().testTag("session-name"),
                    )
                    OutlinedTextField(
                        email,
                        { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        isError = attempted && errors.email != null,
                        supportingText = { if (attempted && errors.email != null) Text(errors.email) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        modifier = Modifier.fillMaxWidth().focusRequester(emailFocus).testTag("session-email"),
                    )
                    Text("Choose a topic", style = MaterialTheme.typography.titleMedium)
                    ChoiceRow(topics, topic, { topic = it })
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text("Include notes preview", modifier = Modifier.weight(1f))
                        Switch(
                            notes,
                            {
                                notes = it
                            },
                            colors =
                                SwitchDefaults.colors(
                                    checkedThumbColor = Instrument.Background,
                                    checkedTrackColor = Instrument.Lime,
                                    uncheckedThumbColor = Instrument.Muted,
                                    uncheckedTrackColor = Instrument.Outline,
                                    uncheckedBorderColor = Instrument.Muted,
                                ),
                            modifier =
                                Modifier.semantics {
                                    contentDescription =
                                        "Include notes preview"
                                },
                        )
                    }
                    Button(onClick = submit, modifier = Modifier.fillMaxWidth().testTag("submit-session")) { Text("Prepare session") }
                    TextButton(onClick = {
                        name = "Alex"
                        email = "alex@example.com"
                        attempted = false
                        focus.clearFocus()
                    }, modifier = Modifier.fillMaxWidth()) { Text("Use fictional example") }
                }
                TextButton(onClick = {
                    name = ""
                    email = ""
                    topic = 0
                    notes = false
                    attempted = false
                    confirmed = false
                    focus.clearFocus()
                }, modifier = Modifier.fillMaxWidth()) { Text("Reset form") }
            }
        }
    }
}
