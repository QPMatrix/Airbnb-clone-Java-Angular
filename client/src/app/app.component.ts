import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Button } from 'primeng/button';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { fontAwesomeIcons } from './shared/font-awesome-icons';
import { NavbarComponent } from './layout/navbar/navbar.component';
import { FooterComponent } from './layout/footer/footer.component';
import { ToastModule } from 'primeng/toast';
import { ToastService } from './layout/toast.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Button, FontAwesomeModule, NavbarComponent, FooterComponent, ToastModule],
  providers:[MessageService],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',

})
export class AppComponent implements OnInit {
  title="Airbnb Clone"
  faIconLibrary: FaIconLibrary = inject(FaIconLibrary);
  toastService: ToastService = inject(ToastService);
  messageService: MessageService = inject(MessageService);
  isListingView: boolean = true;

  ngOnInit(): void {
    this.initFontAwesome();
    this.listenToastService();
  }

  private initFontAwesome() {
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
  }

  private listenToastService(): void {
    this.toastService.sendSubjectObservable.subscribe({
      next: newMessage => {
        if (newMessage && newMessage.summary !== this.toastService.INIT_STATE) {
          this.messageService.add(newMessage);
        }
      },
    });
  }
}
