package com.db.kotlinapp.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "transactions")
class TransactionEntity(

    @Column(nullable = false)
    var date: LocalDate,

    @Column(nullable = false)
    var groceriesTx: Double,

    @Column(nullable = false)
    var groceriesNtx: Double,

    @Column(nullable = false)
    var cigarettes: Double,

    @Column(nullable = false)
    var alcohol: Double,

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Lazy loading ensures performance
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity // ✅ This remains the same
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    constructor() : this(LocalDate.now(), 0.0, 0.0, 0.0, 0.0, UserEntity()) // ✅ Ensures a no-arg constructor
}
