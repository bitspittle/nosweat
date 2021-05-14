package bitspittle.nosweat.model

import com.github.bitspittle.truthish.assertThat
import com.github.bitspittle.truthish.assertThrows
import kotlin.test.Test

class LoginTest {
    @Test
    fun emailParsedIntoUserAndDomain() {
        Email("hey@there").let { email ->
            assertThat(email.user).isEqualTo("hey")
            assertThat(email.domain).isEqualTo("there")
            assertThat(email.value).isEqualTo("hey@there")
        }
    }

    @Test
    fun emailIsLowerCased() {
        Email("hEy@ThErE").let { email ->
            assertThat(email.user).isEqualTo("hey")
            assertThat(email.domain).isEqualTo("there")
            assertThat(email.value).isEqualTo("hey@there")
        }
    }

    @Test
    fun emailMustHaveTwoParts() {
        assertThrows<IllegalArgumentException> {
            Email("")
        }

        assertThrows<IllegalArgumentException> {
            Email("uhoh@")
        }

        assertThrows<IllegalArgumentException> {
            Email("@uhoh")
        }

        assertThrows<IllegalArgumentException> {
            Email("hey@there@uhoh")
        }
    }
}

