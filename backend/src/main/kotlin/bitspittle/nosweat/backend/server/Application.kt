package bitspittle.nosweat.backend.server

import bitspittle.nosweat.model.*
import com.apurebase.kgraphql.GraphQL
import io.ktor.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced by Ktor
fun Application.module(testing: Boolean = false) {
    install(GraphQL) {
        playground = true
        schema {
            configure {
                useDefaultPrettyPrinter = true
            }

            // create query "hero" which returns instance of Character
            query("hero") {
                resolver { episode: Episode ->
                    when (episode) {
                        Episode.NEWHOPE, Episode.JEDI -> r2d2
                        Episode.EMPIRE -> luke
                    }
                }
            }

            // create query "heroes" which returns list of luke and r2d2
            query("heroes") {
                resolver { -> listOf(luke, r2d2) }
            }

            // 1kotlin classes need to be registered with "type" method
            // to be included in created schema type system
            // class Character is automatically included,
            // as it is return type of both created queries
            type<Droid>()
            type<Human>()
            enum<Episode>()
        }
    }
}