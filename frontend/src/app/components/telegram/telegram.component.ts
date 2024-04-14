import { Component, OnInit, inject } from '@angular/core';
import { TelegramService } from '../../services/telegram.service';

@Component({
  selector: 'app-telegram-sender',
  templateUrl: './telegram.component.html',
  styleUrl: './telegram.component.css',
})
export class TelegramComponent implements OnInit {
  
  linkCode!: string;
  private telegramSvc = inject(TelegramService)

  ngOnInit(): void {
    this.generateLinkCode();
    this.storeLinkCode();
  }
  
  generateLinkCode() {
    this.linkCode = Math.random().toString(36).substr(2, 9);
  }

  storeLinkCode() {
    const userId = localStorage.getItem('userId');
    if (userId)
    this.telegramSvc.storeLinkCode(this.linkCode, userId).subscribe({
      next: (response) => console.log('Link code stored successfully'),
      error: (error) => console.error('Error storing link code', error)
    });
  }
}

