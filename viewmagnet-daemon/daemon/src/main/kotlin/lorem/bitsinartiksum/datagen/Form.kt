package lorem.bitsinartiksum.datagen

import lorem.bitsinartiksum.ad.weatherInfo
import model.Weather
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


object Form {

    fun weather(handler: (weatherInfo) -> Unit): JPanel {

        val props = weatherInfo::class.declaredMemberProperties

        val fields = formFields(props.filter { !it.javaField!!.type.isEnum }.map { it.name })
        val combos = formCombos(props
            .filter { it.javaField!!.type.isEnum }
            .map { it.name to it.javaField!!.type.enumConstants }
            .toMap())

        val getText = { name: String -> fields[name]!!.second.text }

        return formView(fields.values.toMap(), combos.values.toMap()) {
            val topic = weatherInfo(
                weather = combos["weather"]?.second?.selectedItem as Weather,
                country = getText("country") ?: "tr",
                sunrise = getText("sunrise").toLongOrNull() ?: 5L,
                sunset = getText("sunset").toLongOrNull() ?: 5L,
                tempC = getText("tempC").toFloatOrNull() ?: 5f,
                timezone = getText("timezone").toIntOrNull() ?: 5,
                windSpeed = getText("windSpeed").toFloatOrNull() ?: 5f
            )
            handler(topic)
        }
    }

    private fun indent(label: String, formPanel: JPanel): JPanel {
        val cont = JPanel(FlowLayout())
        cont.add(JLabel(label))
        cont.add(formPanel)
        return cont
    }

    private fun formView(
        fields: Map<JLabel, JTextField>,
        combos: Map<JLabel, JComboBox<*>>,
        submitHandler: () -> Unit
    ): JPanel {

        val labelPanel = JPanel(GridLayout(fields.size + combos.size, 1))
        val fieldPanel = JPanel(GridLayout(fields.size + combos.size, 1))
        val panel = JPanel(BorderLayout())
        panel.add(labelPanel, BorderLayout.WEST)
        panel.add(fieldPanel, BorderLayout.CENTER)

        fields.entries.forEach { (label, field) ->
            label.labelFor = field
            labelPanel.add(label)
            val fPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            fPanel.add(field)
            fieldPanel.add(fPanel)
        }
        combos.entries.forEach { (label, field) ->
            label.labelFor = field
            labelPanel.add(label)
            val fPanel = JPanel(FlowLayout(FlowLayout.LEFT))
            fPanel.add(field)
            fieldPanel.add(fPanel)
        }
        val submit = JButton("Publish")
        submit.addActionListener { submitHandler() }
        panel.add(submit, BorderLayout.SOUTH)
        return panel
    }

    private fun formFields(labels: List<String>): Map<String, Pair<JLabel, JTextField>> {
        return labels.map {
            val label = JLabel(it, JLabel.RIGHT)
            val field = JTextField(10)
            it to (label to field)
        }.toMap()
    }

    private fun formCombos(enums: Map<String, Array<*>>): Map<String, Pair<JLabel, JComboBox<*>>> {
        return enums.map { (name, vals) ->
            val label = JLabel(name, JLabel.RIGHT)
            val field = JComboBox(vals)
            name to (label to field)
        }.toMap()
    }

}